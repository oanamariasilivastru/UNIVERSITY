# generate_splits.py

import os
import random

def generate_splits(images_dir, labels_dir, splits_dir, train_ratio=0.8):
    image_files = [f for f in os.listdir(images_dir) if f.lower().endswith(('.png', '.jpg', '.jpeg'))]
    image_files.sort()  # Ensure consistent ordering

    random.seed(42)  # For reproducibility
    random.shuffle(image_files)

    num_train = int(len(image_files) * train_ratio)
    train_files = image_files[:num_train]
    val_files = image_files[num_train:]

    # Write train.txt
    with open(os.path.join(splits_dir, 'train.txt'), 'w') as f:
        for img in train_files:
            img_path = os.path.join(images_dir, img)
            f.write(f"{img_path}\n")

    # Write val.txt
    with open(os.path.join(splits_dir, 'val.txt'), 'w') as f:
        for img in val_files:
            img_path = os.path.join(images_dir, img)
            f.write(f"{img_path}\n")

    print(f"Generated {len(train_files)} training samples and {len(val_files)} validation samples.")

if __name__ == "__main__":
    images_dir = r"C:\Users\Oana\Downloads\DarkFace_Train_2021\image"
    labels_dir = r"C:\Users\Oana\Downloads\DarkFace_Train_2021\labels"
    splits_dir = r"C:\Users\Oana\Downloads\DarkFace_Train_2021\splits"

    os.makedirs(splits_dir, exist_ok=True)
    generate_splits(images_dir, labels_dir, splits_dir)
