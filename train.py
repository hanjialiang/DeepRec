from pathlib import Path
import pandas as pd
import numpy as np
import torch
from modules.data import SessionDataset
from modules.model import GRU4REC
import os
import distiller


class Logger(object):
    def __init__(self):
        self._logger = None

    def init(self, logdir, name='log'):
        if self._logger is None:
            import logging
            if not os.path.exists(logdir):
                os.makedirs(logdir)
            log_file = os.path.join(logdir, name)
            if os.path.exists(log_file):
                os.remove(log_file)
            self._logger = logging.getLogger()
            self._logger.setLevel('INFO')
            fh = logging.FileHandler(log_file)
            ch = logging.StreamHandler()
            self._logger.addHandler(fh)
            self._logger.addHandler(ch)

    def info(self, str_info):
        self.init('/log', 'training.log')
        self._logger.info(str_info)
logger = Logger()
print = logger.info


train_dataset = SessionDataset(path = r'./../data/old_user_train.csv')
test_dataset = SessionDataset(path = r'./../data/user_test.csv', itemmap=train_dataset.itemmap)
old_test_dataset = SessionDataset(path = r'./../data/old_user_test.csv', itemmap=train_dataset.itemmap)
new_test_dataset = SessionDataset(path = r'./../data/new_user_test.csv', itemmap=train_dataset.itemmap)

input_size = len(train_dataset.items)
if_embedding = False
embedding_size = 16384
hidden_size = 100
num_layers = 1
output_size = input_size
batch_size = 50

optimizer_type = 'Adagrad'
lr = .01
weight_decay = 0
momentum = 0
eps = 1e-6

loss_types = ['CrossEntropy','BPR','TOP1']

n_epochs = 20
use_cuda = True
cuda_id = 1

compress = u"distiller_scheduler/70_agp.yaml"

torch.manual_seed(7)

for loss_type in loss_types:

    model = GRU4REC(input_size, if_embedding, embedding_size, hidden_size, output_size,
                num_layers=num_layers,
                batch_size=batch_size,
                optimizer_type=optimizer_type,
                lr=lr,
                weight_decay=weight_decay,
                momentum=momentum,
                eps=eps,
                loss_type=loss_type,
                use_cuda=use_cuda,
                cuda_id=cuda_id,
                compress = compress)

    model_name = 'GRU4REC'
    print(loss_type+':')
    model.train(train_dataset, n_epochs=n_epochs, model_name=model_name, save=True)
    print("On All Users:")
    model.test(test_dataset)
    print("On Old Users:")
    model.test(old_test_dataset)
    print("On New Users:")
    model.test(new_test_dataset)
    print()