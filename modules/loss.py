import torch
import torch.nn as nn
from torch.autograd import Variable
import torch.nn.functional as F


class LossFunction(nn.Module):
    def __init__(self, loss_type='TOP1', use_cuda=True, cuda_id=1):
        """ An abstract loss function that can supports custom loss functions compatible with PyTorch."""
        super().__init__()
        self.loss_type = loss_type
        self.use_cuda = use_cuda
        self.cuda_id = cuda_id
        if loss_type == 'CrossEntropy':
            self._loss_fn = SampledCrossEntropyLoss(use_cuda, cuda_id)
        elif loss_type == 'TOP1':
            self._loss_fn = TOP1Loss()
        elif loss_type == 'BPR':
            self._loss_fn = BPRLoss()
        else:
            raise NotImplementedError

    def forward(self, logit):
        return self._loss_fn(logit)


class SampledCrossEntropyLoss(nn.Module):
    """ CrossEntropyLoss with n_classes = batch_size = the number of samples in the session-parallel mini-batch """
    def __init__(self, use_cuda, cuda_id):
        """
        See Balazs Hihasi(ICLR 2016), pg.5

        Args:
             use_cuda (bool): whether to use cuda or not
        """
        super().__init__()
        self.xe_loss = nn.CrossEntropyLoss()
        self.use_cuda = use_cuda
        self.cuda_id = cuda_id

    def forward(self, logit):
        batch_size = logit.size(1)
        target = Variable(torch.arange(batch_size).long())
        if self.use_cuda: target = target.cuda(self.cuda_id)

        return self.xe_loss(logit, target)


class BPRLoss(nn.Module):
    def __init__(self):
        """
        See Balazs Hihasi(ICLR 2016), pg.5
        """
        super().__init__()

    def forward(self, logit):
        """
        Args:
            logit (BxB): Variable that stores the logits for the items in the session-parallel mini-batch.
                         Negative samples for a specific item are drawn from the other items in the
                         session-parallel minibatch, as mentioned in the original GRU4REC paper.
                         The first dimension corresponds to the batches, and the second dimension
                         corresponds to sampled number of items to evaluate.
        """

        # differences between the item scores
        diff = logit.diag().view(-1, 1).expand_as(logit) - logit
        # final loss
        loss = -torch.mean(F.logsigmoid(diff))

        return loss


class TOP1Loss(nn.Module):
    def __init__(self):
        """
        See Balazs Hihasi(ICLR 2016), pg.5
        """
        super().__init__()

    def forward(self, logit):
        """
        Args:
            logit (BxB): Variable that stores the logits for the items in the session-parallel mini-batch.
                         Negative samples for a specific item are drawn from the other items in the
                         session-parallel minibatch, as mentioned in the original GRU4REC paper.
                         The first dimension corresponds to the batches, and the second dimension
                         corresponds to sampled number of items to evaluate.
        """
        # differences between the item scores
        diff = -(logit.diag().view(-1, 1).expand_as(logit) - logit)
        # final loss
        loss = F.sigmoid(diff).mean() + F.sigmoid(logit ** 2).mean()

        return loss
