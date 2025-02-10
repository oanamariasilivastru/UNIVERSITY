import torch
import torch.nn as nn
import torch.optim as optim
from torch.utils.data import DataLoader

# This script demonstrates training the geometry-based CNN alone.
# You need a dataset that provides facial landmarks or a placeholder approach.

from models.geometry_cnn_scratch import GeometryCNNScratch

# We'll make a dummy dataset to illustrate. Replace this with a real dataset that
# loads your 68-landmark differences as 136 values, plus a label for the emotion.

class DummyGeometryDataset(torch.utils.data.Dataset):
    """
    A placeholder dataset that generates random 1D geometry vectors (136 dims)
    and random emotion labels. Replace with real landmark data in practice.
    """
    def __init__(self, num_samples=1000, num_classes=7):
        super().__init__()
        import numpy as np
        self.num_samples = num_samples
        self.num_classes = num_classes
        
        # Generate random geometry and random labels
        self.data = np.random.rand(num_samples, 136).astype(np.float32)  # shape [num_samples, 136]
        self.labels = np.random.randint(low=0, high=num_classes, size=(num_samples,))
    
    def __len__(self):
        return self.num_samples
    
    def __getitem__(self, idx):
        geometry_vector = self.data[idx]  # shape [136]
        label = self.labels[idx]
        
        # Convert to torch tensors
        geom_tensor = torch.tensor(geometry_vector).unsqueeze(0)  # shape [1, 136]
        label_tensor = torch.tensor(label, dtype=torch.long)
        
        return geom_tensor, label_tensor

def train_epoch(model, loader, criterion, optimizer, device):
    model.train()
    running_loss = 0.0
    correct = 0
    total = 0
    
    for geometry, labels in loader:
        geometry, labels = geometry.to(device), labels.to(device)
        
        optimizer.zero_grad()
        outputs = model(geometry)  # shape [batch_size, num_classes]
        loss = criterion(outputs, labels)
        loss.backward()
        optimizer.step()
        
        running_loss += loss.item() * geometry.size(0)
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
        for geometry, labels in loader:
            geometry, labels = geometry.to(device), labels.to(device)
            outputs = model(geometry)
            loss = criterion(outputs, labels)
            
            running_loss += loss.item() * geometry.size(0)
            _, preds = torch.max(outputs, dim=1)
            correct += (preds == labels).sum().item()
            total += labels.size(0)
    
    epoch_loss = running_loss / total
    epoch_acc = correct / total
    return epoch_loss, epoch_acc

def main():
    device = torch.device("cuda" if torch.cuda.is_available() else "cpu")
    
    # Example: 7 emotion classes
    num_classes = 7
    
    # 1) Create dummy train & val sets
    train_dataset = DummyGeometryDataset(num_samples=1000, num_classes=num_classes)
    val_dataset = DummyGeometryDataset(num_samples=200, num_classes=num_classes)
    
    train_loader = DataLoader(train_dataset, batch_size=32, shuffle=True)
    val_loader = DataLoader(val_dataset, batch_size=32, shuffle=False)
    
    # 2) Create the geometry-based CNN from scratch
    model = GeometryCNNScratch(num_classes=num_classes).to(device)
    
    # 3) Criterion & optimizer
    criterion = nn.CrossEntropyLoss()
    optimizer = optim.Adam(model.parameters(), lr=1e-4)
    
    # 4) Train loop
    epochs = 5
    for epoch in range(epochs):
        train_loss, train_acc = train_epoch(model, train_loader, criterion, optimizer, device)
        val_loss, val_acc = eval_epoch(model, val_loader, criterion, device)
        print(f"Epoch [{epoch+1}/{epochs}]")
        print(f"  Train Loss: {train_loss:.4f}, Train Acc: {train_acc:.4f}")
        print(f"  Val Loss:   {val_loss:.4f},   Val Acc:   {val_acc:.4f}")
    
    # 5) Save
    torch.save(model.state_dict(), "emotion_model.pth")

if __name__ == "__main__":
    main()
