import yaml
import wandb
import pickle as pk
from app.utils.file_utils import get_file_name

# Load configuration file
with open('config.yaml', 'r') as file:
    config = yaml.safe_load(file)

# Start a new wandb run to track this script
wandb.init(
    project="SimQuery",
    config=config
)

# Get and preprocess data
# X, y = get_data(path)

# Create model
# model = ..

# Train and predict
# model.train()
# y_pred = model.predict()

# Save model
file_path = get_file_name('', config['model']['name'])

file = open(file_path, 'wb')
# pk.dump(model, file, protocol=pk.HIGHEST_PROTOCOL)

# Calculate evaluation metrics
# evals = model.get_evaluation_metrics()

# Finish the Wandb run
wandb.finish()