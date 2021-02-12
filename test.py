from pathlib import Path
from modules.data import SessionDataset
import pandas as pd
import numpy as np
import os
import torch
from modules.layer import GRU
from modules.model import GRU4REC

train_dataset = SessionDataset(path = r'./../data/old_user_train.csv')
test_dataset = SessionDataset(path = r'./../data/user_test.csv', itemmap=train_dataset.itemmap)
old_test_dataset = SessionDataset(path = r'./../data/old_user_test.csv', itemmap=train_dataset.itemmap)
new_test_dataset = SessionDataset(path = r'./../data/new_user_test.csv', itemmap=train_dataset.itemmap)

input_size = len(train_dataset.items)
output_size = input_size
hidden_size = 100
num_layers = 1
batch_size = 50

if_embedding = False
embedding_size = 10000
use_cuda = True
time_sort = False
cuda_id = 1

optimizer_type = 'Adagrad'
lr = .01
weight_decay = 0
momentum = 0
eps = 1e-6

loss_type = 'CrossEntropy'
lr = 0.01

dropout_hidden = 0
dropout_input = 0
batch_size = 50
momentum = 0

gru = GRU(input_size, if_embedding, embedding_size, hidden_size, output_size,
          num_layers = num_layers,
          dropout_input = dropout_input,
          dropout_hidden = dropout_hidden,
          batch_size = batch_size,
          use_cuda = use_cuda,
          cuda_id = cuda_id)

print(loss_type + ':')
for i in range(1, 21):
    model_name = 'GRU4REC_CrossEntropy_Adagrad_0.01_epoch%d' % i
    print(model_name)
    model_file = r'./models/' + model_name
    gru.load_state_dict(torch.load(model_file))
    
    model = GRU4REC(input_size, if_embedding, embedding_size, hidden_size, output_size,
                num_layers=num_layers,
                dropout_input = dropout_input,
                dropout_hidden = dropout_hidden,
                batch_size=batch_size,
                optimizer_type=optimizer_type,
                lr=lr,
                weight_decay=weight_decay,
                momentum=momentum,
                eps=eps,
                loss_type=loss_type,
                use_cuda=use_cuda,
                cuda_id=cuda_id,
                time_sort=time_sort,
                pretrained=gru)


    k = 20
    print("On All Users:")
    model.test(test_dataset,k=k)
    print("On Old Users:")
    model.test(old_test_dataset,k=k)
    print("On New Users:")
    model.test(new_test_dataset,k=k)
    print()