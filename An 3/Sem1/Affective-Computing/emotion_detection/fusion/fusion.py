import torch
import torch.nn as nn

class FusionScratch(nn.Module):
    """
    A from-scratch fusion model that combines:
      - MobileNetV2Scratch for global facial features
      - GeometryCNNScratch for landmark geometry
    We extract intermediate features from both networks, then concatenate
    them and apply a final classifier.
    """
    def __init__(self, mobilenet_model, geometry_model, num_classes=8):
        super(FusionScratch, self).__init__()
        
        self.mobilenet = mobilenet_model       # instance of MobileNetV2Scratch
        self.geometry_net = geometry_model     # instance of GeometryCNNScratch
        
        # We want to remove the final classification layer from each model to get
        # their "penultimate" features. Then we'll do a new FC for fusion.
        
        # Typically, MobileNetV2Scratch has:
        #  -> self.features for the backbone
        #  -> self.classifier (a linear layer).
        # We'll keep the backbone, remove the final linear, and treat that as a feature extractor.
        
        # For geometry_net, we also want to stop before the final fc2. We'll handle that in forward().

        # The final dimension of MobileNetV2Scratch features is self.mobilenet.last_channel (e.g., 1280).
        self.mobilenet_feature_dim = self.mobilenet.last_channel
        
        # The geometry net produces 128 features right before fc2 (we'll extract that).
        self.geometry_feature_dim = 128
        
        # Build a new classification layer after concatenating both feature vectors
        self.fusion_fc = nn.Sequential(
            nn.Linear(self.mobilenet_feature_dim + self.geometry_feature_dim, 256),
            nn.ReLU(),
            nn.Dropout(p=0.5),
            nn.Linear(256, num_classes)
        )

    def forward(self, img, geo):
        """
        img: [batch_size, 3, H, W]
        geo: [batch_size, 1, 136] (example for 68 landmarks)
        """
        # 1) Extract features from MobileNetV2Scratch (excluding final FC)
        # We'll manually replicate part of the forward pass to stop early.
        # Or we can do a slight rewrite in the MobileNet class to handle that.

        # We'll do something like:
        x_img = self.mobilenet.features(img)     # => [batch_size, last_channel, 7, 7] if 224x224
        x_img = x_img.mean([2, 3])               # global average => [batch_size, last_channel]
        # skip self.mobilenet.classifier since we only want features

        # 2) Extract geometry features up to the penultimate layer
        x_geo = self.extract_geometry_features(geo)  # => [batch_size, 128]

        # 3) Concatenate
        fused = torch.cat((x_img, x_geo), dim=1)  # => [batch_size, 1280 + 128]

        # 4) Final classification
        out = self.fusion_fc(fused)
        return out

    def extract_geometry_features(self, x):
        """
        Manually replicate geometry_net forward minus the final classification layer.
        geometry_net: conv1 -> relu1 -> pool1 -> conv2 -> relu2 -> pool2 -> flatten -> fc1 -> dropout -> fc2
        We want to stop before fc2, so we retrieve the 128-dim features.
        """
        x = self.geometry_net.conv1(x)
        x = self.geometry_net.relu1(x)
        x = self.geometry_net.pool1(x)

        x = self.geometry_net.conv2(x)
        x = self.geometry_net.relu2(x)
        x = self.geometry_net.pool2(x)

        x = x.view(x.size(0), -1)
        x = self.geometry_net.relu3(self.geometry_net.fc1(x))
        x = self.geometry_net.dropout(x)
        return x
