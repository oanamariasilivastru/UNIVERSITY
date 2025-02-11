import os
import random
from pathlib import Path
import argparse

def parse_arguments():
    parser = argparse.ArgumentParser(description="Pregătirea dataset-ului DarkFace pentru antrenare.")
    parser.add_argument('--dataset_dir', type=str, required=True,
                        help='Calea către directorul dataset-ului (DarkFace_Train_2021).')
    parser.add_argument('--val_split', type=float, default=0.2,
                        help='Proporția de date pentru validare (default: 0.2).')
    parser.add_argument('--output_dir', type=str, default=None,
                        help='Directorul unde se vor salva fișierele train.txt și val.txt. Implicit: dataset_dir')
    return parser.parse_args()


def get_image_label_pairs(dataset_dir):
    image_dir = Path(dataset_dir) / 'image'
    label_dir = Path(dataset_dir) / 'label'

    pairs = []
    for img_path in sorted(image_dir.glob("*.png")):  # Căutăm fișiere .png
        label_path = label_dir / f"{img_path.stem}.txt"
        if label_path.exists():
            pairs.append((img_path, label_path))
        else:
            print(f"Avertisment: Fișierul de etichetă {label_path} nu există pentru {img_path}")

    return pairs


def split_dataset(pairs, val_split=0.2):
    random.shuffle(pairs)
    val_size = int(len(pairs) * val_split)
    val_pairs = pairs[:val_size]
    train_pairs = pairs[val_size:]
    return train_pairs, val_pairs


def write_split(pairs, file_path):
    with open(file_path, 'w') as f:
        for img_path, label_path in pairs:
            f.write(f"{img_path} {label_path}\n")
    print(f"Scris: {len(pairs)} în {file_path}")


def main():
    args = parse_arguments()

    dataset_dir = Path(args.dataset_dir)
    output_dir = Path(args.output_dir) if args.output_dir else dataset_dir
    val_split = args.val_split

    if not (dataset_dir / 'image').is_dir() or not (dataset_dir / 'label').is_dir():
        raise FileNotFoundError("Directorul image sau label nu există în calea specificată.")

    pairs = get_image_label_pairs(dataset_dir)
    print(f"Total imagini cu etichete: {len(pairs)}")

    train_pairs, val_pairs = split_dataset(pairs, val_split)

    write_split(train_pairs, output_dir / 'train.txt')
    write_split(val_pairs, output_dir / 'val.txt')


if __name__ == "__main__":
    main()
