# -*- coding:utf-8 -*-

import os
import random
import numpy as np
import torch
import torch.utils.data as data
from PIL import Image
from augmentations import preprocess

class DARKFaceDetection(data.Dataset):
    """DARKFaceDetection - încarcă perechi imagine-etichetă pentru detecția fețelor"""

    def __init__(self, list_file, mode='train'):
        super(DARKFaceDetection, self).__init__()
        self.mode = mode
        self.data = []

        # Citește fișierele din train.txt / val.txt
        with open(list_file) as f:
            lines = f.readlines()

        # Stochează perechile imagine-eticheta
        for line in lines:
            img_path, label_path = line.strip().split()
            if os.path.exists(img_path) and os.path.exists(label_path):
                self.data.append((img_path, label_path))

        self.num_samples = len(self.data)
        print(f"Loaded {self.num_samples} samples from {list_file}")

    def __len__(self):
        return self.num_samples

    def __getitem__(self, index):
        img, face_target, head_target = self.pull_item(index)
        return img, face_target, head_target

    def pull_item(self, index):
        img_path, label_path = self.data[index]
        img = Image.open(img_path).convert("RGB")

        # Citește coordonatele bounding box din fișierul label
        with open(label_path, 'r') as f:
            lines = f.readlines()

        boxes = []
        labels = []
        for line in lines[1:]:  # Ignoră prima linie (numărul de fețe)
            x_min, y_min, x_max, y_max = map(float, line.split())
            boxes.append([x_min, y_min, x_max, y_max])
            labels.append(1)  # Clasa 1 = față

        if len(boxes) == 0:
            boxes = [[0, 0, 1, 1]]  # Dacă nu există față, se adaugă o cutie default
            labels = [0]  # Clasificată ca "fără față"

        im_width, im_height = img.size
        boxes = self.annotransform(np.array(boxes), im_width, im_height)

        bbox_labels = np.hstack((np.array(labels)[:, np.newaxis], boxes)).tolist()
        img, sample_labels = preprocess(img, bbox_labels, self.mode, img_path)

        face_target = np.hstack((sample_labels[:, 1:], sample_labels[:, 0][:, np.newaxis]))
        head_box = self.expand_bboxes(face_target[:, :-1])

        head_target = np.hstack((head_box, face_target[:, -1][:, np.newaxis]))
        return torch.from_numpy(img), face_target, head_target

    def annotransform(self, boxes, im_width, im_height):
        # Normalizează box-urile la 0-1
        boxes[:, 0] /= im_width
        boxes[:, 1] /= im_height
        boxes[:, 2] /= im_width
        boxes[:, 3] /= im_height
        return boxes

    def expand_bboxes(self, bboxes, expand_ratio=1.5):
        # Expandează bounding box-urile
        expand_bboxes = []
        for bbox in bboxes:
            xmin, ymin, xmax, ymax = bbox
            w = xmax - xmin
            h = ymax - ymin

            ex_xmin = max(xmin - w / expand_ratio, 0.)
            ex_ymin = max(ymin - h / expand_ratio, 0.)
            ex_xmax = min(xmax + w / expand_ratio, 1.)
            ex_ymax = min(ymax + h / expand_ratio, 1.)

            expand_bboxes.append([ex_xmin, ex_ymin, ex_xmax, ex_ymax])
        return np.array(expand_bboxes)


def detection_collate(batch):
    face_targets = []
    head_targets = []
    imgs = []

    for sample in batch:
        imgs.append(sample[0])
        face_targets.append(torch.FloatTensor(sample[1]))
        head_targets.append(torch.FloatTensor(sample[2]))
    return torch.stack(imgs, 0), face_targets, head_targets


if __name__ == '__main__':
    from config import cfg

    dataset = DARKFaceDetection(
        os.path.join(cfg.FACE.DSET_DIR, 'train.txt'),
        mode='train'
    )
    img, face_target, head_target = dataset[0]
    print(f"Loaded image shape: {img.shape}")
    print(f"Face target: {face_target}")
    print(f"Head target: {head_target}")
