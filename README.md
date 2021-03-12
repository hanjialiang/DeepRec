# DeepRec

This repository contains code for the paper **DeepRec: On-device Deep Learning for Privacy-Preserving Sequential Recommendation in Mobile Commerce**.

If you find this code useful, please cite our work:

```latex
@inproceedings{han2021deeprec,
  title={DeepRec: On-device Deep Learning for Privacy-Preserving Sequential Recommendation in Mobile Commerce},
  author={Han, Jialiang and Ma, Yun and Mei, Qiaozhu and Liu, Xuanzhe},
  booktitle={Proceedings of the Web Conference 2021 (WWW '21), April 19--23, 2021, Ljubljana, Slovenia},
  year={2021}
}
```

## DeepRec

![Approach](https://github.com/hanjialiang/DeepRec/blob/main/Approach.png)

First, we train a Gated Recurrent Unit (GRU) based recommendation model, i.e. the global model, with existing interaction behavior data of all users collected *before* GDPR, extract a personal recommendation item candidate set for each user through a collaborative filtering based model on the cloud, and push the global model to individual devices. Second, we perform fine-tuning over a GRU-based recommendation model, i.e. the personal model, with interaction behavior data of each user collected *after* GDPR on her own device, and extract a unique user-specific embedding from her personal model. Third, a user's device pulls her recommendation item candidate set from the cloud, and then we calculate the inner products of her user embedding and candidate item embeddings to acquire scores representing probabilities she would like to click an item in the current session. Note that, *fine-tuning* in the personal training phase means freezing the layers before the last GRU layer in the global model, and re-training only the last GRU layer and fully connected layer(s). The intuition behind fine-tuning is that the first several GRU layers can generalize from the global training set to the personal training set. So only the last GRU layer and fully connected layer(s) require to be re-trained, to fit the user-specific next click prediction.

## Dataset and Requirements

[UserBehavior](https://tianchi.aliyun.com/dataset/dataDetail?dataId=649])

[Pytorch](https://pytorch.org/)

[Distiller](https://intellabs.github.io/distiller/)

[TensorFlow.js](https://www.tensorflow.org/js)

numpy, pandas, ipdb