# import yaml
# import wandb
# import pickle as pk
# from keras._tf_keras.keras.preprocessing.text import Tokenizer

# from app.utils.file_utils import get_file_name

# # Load configuration file
# with open('../../../config.yaml', 'r') as file:
#     config = yaml.safe_load(file)

# # Start a new wandb run to track this script
# run = wandb.init(
#     project="SimQuery",
#     config=config
# )

# # Read data
# train_url = '../../../data/sts_train.csv'
# test_url = '../../../data/sts_test.csv'
# dev_url = '../../../data/sts_dev.csv'
# train = prepare_dataset(train_url)
# test = prepare_dataset(test_url)
# dev = prepare_dataset(dev_url)

# # Tokenize data
# tokenizer = Tokenizer(char_level=True)
# tokenizer.fit_on_texts(train['sent_1'])
# train_text2seq_1, train_text2seq_2, train_label = prepere_training_data(train, tokenizer)
# test_text2seq_1, test_text2seq_2, test_label = prepere_training_data(test, tokenizer)
# dev_text2seq_1, dev_text2seq_2, dev_label = prepere_training_data(dev, tokenizer)