import os
import glob
from PIL import Image
import torch
from torch.utils.data import Dataset

class AffectNetDataset(Dataset):

    def __init__(self, root_dir, split='train', transform=None):
        super().__init__()
        self.root_dir = os.path.join(root_dir, split)
        self.transform = transform
        
        # We assume each emotion has its own folder
        self.classes = sorted(os.listdir(self.root_dir))
        
        self.samples = []
        for class_index, class_name in enumerate(self.classes):
            class_folder = os.path.join(self.root_dir, class_name)
            image_paths = glob.glob(os.path.join(class_folder, "*.jpg")) + \
                          glob.glob(os.path.join(class_folder, "*.png"))
            for img_path in image_paths:
                self.samples.append((img_path, class_index))
        
    def __len__(self):
        return len(self.samples)
    
    def __getitem__(self, index):
        img_path, label = self.samples[index]
        image = Image.open(img_path).convert('RGB')
        
        if self.transform is not None:
            image = self.transform(image)
        
        return image, label
