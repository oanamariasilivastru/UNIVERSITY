# data/dataset.py

import os
import torch
from torch.utils.data import Dataset
from PIL import Image
import numpy as np
from utils.augmentations import preprocess

class DARKFaceDetection(Dataset):
    def __init__(self, root, list_file, mode='train'):
        """
        Args:
            root (string): Root directory of images.
            list_file (string): Path to the text file with annotations.
            mode (string): 'train' or 'test'.
        """
        self.root = root
        self.mode = mode
        self.imgs = []
        self.labels = []

        with open(list_file, 'r') as f:
            lines = f.readlines()
            for line in lines:
                parts = line.strip().split()
                img_path = os.path.join(root, parts[0])
                num_objs = int(parts[1])
                objs = []
                for i in range(num_objs):
                    idx = 2 + i * 5
                    label = int(parts[idx + 4])  # Assuming class label is the 5th element
                    bbox = [float(x) for x in parts[idx:idx + 4]]
                    objs.append([label] + bbox)
                self.imgs.append(img_path)
                self.labels.append(objs)

    def __len__(self):
        return len(self.imgs)

    def __getitem__(self, idx):
        """
        Returns:
            image (tensor): Processed image tensor.
            face_target (list of tensors): Bounding boxes for faces.
            head_target (list of tensors): Bounding boxes for heads (if applicable).
        """
        img_path = self.imgs[idx]
        label = self.labels[idx]

        # Load image
        img = Image.open(img_path).convert('RGB')
        img_width, img_height = img.size

        # Preprocess image and annotations
        img, face_labels = preprocess(img, label, self.mode, img_path)

        # Convert to tensor
        img = torch.from_numpy(img).float()

        # Split face and head annotations if necessary
        # Assuming label format: [class, xmin, ymin, xmax, ymax, ...]
        # Modify as per your annotation structure
        face_targets = face_labels  # Modify if you have separate targets

        # Placeholder for head_targets; modify if applicable
        head_targets = []

        return img, face_targets, head_targets

