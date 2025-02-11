import torchvision.transforms as T

def get_fer2013_transforms():
    """
    Example transforms for FER2013 images (often 48x48 grayscale).
    We can keep them as grayscale or convert to RGB. Adjust as needed.
    """
    train_transform = T.Compose([
        T.Resize((48, 48)),               # standardize size
        T.RandomHorizontalFlip(p=0.5),    # data augmentation
        T.RandomRotation(degrees=15),     # small rotation
        T.ToTensor(),
        T.Normalize(mean=[0.5], std=[0.5]) # if grayscale, single-channel
    ])
    
    val_transform = T.Compose([
        T.Resize((48, 48)),
        T.ToTensor(),
        T.Normalize(mean=[0.5], std=[0.5])
    ])
    
    return train_transform, val_transform

def get_affectnet_transforms():
    """
    Example transforms for AffectNet images (in practice, often 96x96 or 224x224).
    Adjust to your actual training resolution and needs.
    """
    train_transform = T.Compose([
        T.Resize((96, 96)),
        T.RandomHorizontalFlip(p=0.5),
        T.RandomRotation(degrees=15),
        T.ToTensor(),
        T.Normalize(mean=[0.5, 0.5, 0.5], 
                    std=[0.5, 0.5, 0.5])
    ])
    
    val_transform = T.Compose([
        T.Resize((96, 96)),
        T.ToTensor(),
        T.Normalize(mean=[0.5, 0.5, 0.5],
                    std=[0.5, 0.5, 0.5])
    ])
    
    return train_transform, val_transform
