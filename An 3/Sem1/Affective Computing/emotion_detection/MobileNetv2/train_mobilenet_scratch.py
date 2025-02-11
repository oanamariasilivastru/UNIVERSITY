import torch
import torch.nn as nn
import torch.optim as optim
from torch.utils.data import DataLoader

# Example: We train MobileNetV2 from scratch on AffectNet (8 classes).
# You can adapt to FER2013 or another dataset as needed.

from datasets.dataset_affectnet import AffectNetDataset
from datasets.transforms_data import get_affectnet_transforms
from models.mobilenetv2_scratch import MobileNetV2Scratch

def train_epoch(model, loader, criterion, optimizer, device):
    model.train()
    running_loss = 0.0
    correct = 0
    total = 0
    
    for images, labels in loader:
        images, labels = images.to(device), labels.to(device)
        
        optimizer.zero_grad()
        outputs = model(images)
        loss = criterion(outputs, labels)
        loss.backward()
        optimizer.step()
        
        running_loss += loss.item() * images.size(0)
        _, preds = torch.max(outputs, dim=1)
        correct += (preds == labels).sum().item()
        total += labels.size(0)
    
    epoch_loss = running_loss / total
    epoch_acc = correct / total
    return epoch_loss, epoch_acc

def eval_epoch(model, loader, criterion, device):
    model.eval()
    running_loss = 0.0
    correct = 0
    total = 0
    
    with torch.no_grad():
        for images, labels in loader:
            images, labels = images.to(device), labels.to(device)
            outputs = model(images)
            loss = criterion(outputs, labels)
            
            running_loss += loss.item() * images.size(0)
            _, preds = torch.max(outputs, dim=1)
            correct += (preds == labels).sum().item()
            total += labels.size(0)
    
    epoch_loss = running_loss / total
    epoch_acc = correct / total
    return epoch_loss, epoch_acc

def main():
    device = torch.device("cuda" if torch.cuda.is_available() else "cpu")
    
    # 1) Prepare the AffectNet dataset & transforms
    train_transform, val_transform = get_affectnet_transforms()
    
    train_dataset = AffectNetDataset(
        root_dir="../Users/Oana/Datasets/AffectNet",
        split='train',
        transform=train_transform
    )
    val_dataset = AffectNetDataset(
        root_dir="../Users/Oana/Datasets/AffectNet",
        split='val',
        transform=val_transform
    )
    
    train_loader = DataLoader(train_dataset, batch_size=32, shuffle=True, num_workers=4)
    val_loader = DataLoader(val_dataset, batch_size=32, shuffle=False, num_workers=4)
    
    # 2) Create MobileNetV2 (from scratch)
    model = MobileNetV2Scratch(num_classes=8).to(device)  # 8 classes for AffectNet
    
    # 3) Define loss & optimizer
    criterion = nn.CrossEntropyLoss()
    optimizer = optim.Adam(model.parameters(), lr=1e-4)
    
    # 4) Training loop
    epochs = 10
    for epoch in range(epochs):
        train_loss, train_acc = train_epoch(model, train_loader, criterion, optimizer, device)
        val_loss, val_acc = eval_epoch(model, val_loader, criterion, device)
        print(f"Epoch [{epoch+1}/{epochs}]")
        print(f"  Train Loss: {train_loss:.4f}, Train Acc: {train_acc:.4f}")
        print(f"  Val Loss:   {val_loss:.4f},   Val Acc:   {val_acc:.4f}")
    
    # 5) Save the model
    torch.save(model.state_dict(), "mobilenetv2_scratch_affectnet.pth")

if __name__ == "__main__":
    main()
