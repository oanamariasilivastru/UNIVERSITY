import torch
import torch.nn as nn

class VGG16Scratch(nn.Module):
    """
    A from-scratch implementation of the VGG16 architecture.

    Reference:
    - "Very Deep Convolutional Networks for Large-Scale Image Recognition"
      (Simonyan and Zisserman, 2014)
    
    Architecture Outline:
    - 13 convolutional layers + 3 fully connected => total 16 weight layers.
    - Each convolution is 3x3, stride=1, padding=1, followed by ReLU.
    - MaxPool (2x2, stride=2) after a few blocks.
    - Final 3 FC layers (FC1=4096, FC2=4096, FC3=num_classes).
    """

    def __init__(self, num_classes=7, init_weights=True):
        super(VGG16Scratch, self).__init__()

        # Convolutional part (features)
        self.features = nn.Sequential(
            # Block 1
            nn.Conv2d(3, 64, kernel_size=3, padding=1),
            nn.ReLU(inplace=True),
            nn.Conv2d(64, 64, kernel_size=3, padding=1),
            nn.ReLU(inplace=True),
            nn.MaxPool2d(kernel_size=2, stride=2),

            # Block 2
            nn.Conv2d(64, 128, kernel_size=3, padding=1),
            nn.ReLU(inplace=True),
            nn.Conv2d(128, 128, kernel_size=3, padding=1),
            nn.ReLU(inplace=True),
            nn.MaxPool2d(kernel_size=2, stride=2),

            # Block 3
            nn.Conv2d(128, 256, kernel_size=3, padding=1),
            nn.ReLU(inplace=True),
            nn.Conv2d(256, 256, kernel_size=3, padding=1),
            nn.ReLU(inplace=True),
            nn.Conv2d(256, 256, kernel_size=3, padding=1),
            nn.ReLU(inplace=True),
            nn.MaxPool2d(kernel_size=2, stride=2),

            # Block 4
            nn.Conv2d(256, 512, kernel_size=3, padding=1),
            nn.ReLU(inplace=True),
            nn.Conv2d(512, 512, kernel_size=3, padding=1),
            nn.ReLU(inplace=True),
            nn.Conv2d(512, 512, kernel_size=3, padding=1),
            nn.ReLU(inplace=True),
            nn.MaxPool2d(kernel_size=2, stride=2),

            # Block 5
            nn.Conv2d(512, 512, kernel_size=3, padding=1),
            nn.ReLU(inplace=True),
            nn.Conv2d(512, 512, kernel_size=3, padding=1),
            nn.ReLU(inplace=True),
            nn.Conv2d(512, 512, kernel_size=3, padding=1),
            nn.ReLU(inplace=True),
            nn.MaxPool2d(kernel_size=2, stride=2),
        )

        # Classifier part (fully connected)
        # Assuming input images are 224x224, the output of the conv part is 7x7x512
        self.classifier = nn.Sequential(
            nn.Linear(512 * 7 * 7, 4096),
            nn.ReLU(inplace=True),
            nn.Dropout(p=0.5),

            nn.Linear(4096, 4096),
            nn.ReLU(inplace=True),
            nn.Dropout(p=0.5),

            nn.Linear(4096, num_classes)
        )

        # Optional weight initialization
        if init_weights:
            self._initialize_weights()

    def forward(self, x):
        # Extract features
        x = self.features(x)
        # Flatten
        x = x.view(x.size(0), -1)
        # Classify
        x = self.classifier(x)
        return x

    def _initialize_weights(self):
        """
        Initializes weights in a way that's typically used in modern PyTorch.
        The original VGG used random Gaussian, but we'll use Kaiming or Xavier for stability.
        """
        for m in self.modules():
            if isinstance(m, nn.Conv2d):
                nn.init.kaiming_normal_(m.weight, mode='fan_out', nonlinearity='relu')
                if m.bias is not None:
                    nn.init.constant_(m.bias, 0)
            elif isinstance(m, nn.Linear):
                nn.init.kaiming_uniform_(m.weight)
                nn.init.constant_(m.bias, 0)
