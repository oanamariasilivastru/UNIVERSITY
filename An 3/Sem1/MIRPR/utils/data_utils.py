import pandas as pd


def read_data(file_path):
    '''
    Function to read the data from the csv file.
    :param file_path: your file path
    :return: a pandas dataframe
    '''
    df = pd.read_csv(file_path, delimiter='\t', quotechar='"')
    return df


def plot_learning_curve(history, filepath):
    import matplotlib.pyplot as plt

    # Extract values
    epochs = range(1, len(history['loss']) + 1)
    training_loss = history['loss']
    val_loss = history['val_loss']
    training_accuracy = history.get('accuracy', None)
    val_accuracy = history.get('val_accuracy', None)

    # Create the plot
    plt.figure(figsize=(10, 6))

    # Plot losses
    plt.plot(epochs, training_loss, 'r-', label='Training Loss', linewidth=2)
    plt.plot(epochs, val_loss, 'g--', label='Validation Loss', linewidth=2)

    # Plot accuracies if they exist
    if training_accuracy:
        plt.plot(epochs, training_accuracy, 'b-', label='Training Accuracy', linewidth=2)
    if val_accuracy:
        plt.plot(epochs, val_accuracy, 'y--', label='Validation Accuracy', linewidth=2)

    # Add title and labels
    plt.title("Training and Validation Metrics", fontsize=16)
    plt.xlabel("Epochs", fontsize=14)
    plt.ylabel("Metric Value", fontsize=14)

    # Add a legend
    plt.legend(fontsize=12)
    plt.grid(True, linestyle='--', alpha=0.6)

    # Display the plot
    plt.tight_layout()
    plt.savefig(filepath)
    plt.show()
