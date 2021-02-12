import torch
import torch.nn as nn


class GRU(nn.Module):

    def __init__(self, input_size, if_embedding, embedding_size, hidden_size, output_size, num_layers=1,
                 dropout_hidden=.5, dropout_input=0, batch_size=50, use_cuda=True, cuda_id=1):
        '''
        The GRU layer used for the whole GRU4REC model.

        Args:
            input_size (int): input layer dimension
            hidden_size (int): hidden layer dimension
            output_size (int): output layer dimension. Equivalent to the number of classes
            num_layers (int): the number of GRU layers
            dropout_hidden (float): dropout probability for the GRU hidden layers
            dropout_input (float): dropout probability for the GRU input layer
            batch_size (int): size of the training batch.(required for producing one-hot encodings efficiently)
            use_cuda (bool): whether to use cuda or not
            training (bool): whether to set the GRU module to training mode or not. If false, parameters will not be updated.
        '''

        super().__init__()
        self.input_size = input_size
        self.if_embedding = if_embedding
        self.embedding_size = embedding_size
        self.hidden_size = hidden_size
        self.output_size = output_size
        self.num_layers = num_layers
        self.dropout_input = dropout_input
        self.dropout_hidden = dropout_hidden

        self.batch_size = batch_size
        self.use_cuda = use_cuda
        self.cuda_id = cuda_id
        self.device = torch.device('cuda:%d'%cuda_id if use_cuda else 'cpu')
        print(self.device)

        self.onehot_buffer = self.init_emb()  # the buffer where the one-hot encodings will be produced from
        # self.i2e = nn.Linear(input_size, embedding_size)
        self.h2o = nn.Linear(hidden_size, output_size)
        self.tanh = nn.Tanh()
        #if if_embedding:
        #    self.gru = nn.GRU(embedding_size, hidden_size, num_layers, dropout=dropout_hidden)
        #else:
        self.gru = nn.GRU(input_size, hidden_size, num_layers, dropout=dropout_hidden)
        
        self = self.to(self.device)
        

    def forward(self, input, target, hidden):
        '''
        Args:
            input (B,): a batch of item indices from a session-parallel mini-batch.
            target (B,): torch.LongTensor of next item indices from a session-parallel mini-batch.
            
        Returns:
            logit (B,C): Variable that stores the logits for the next items in the session-parallel mini-batch
            hidden: GRU hidden state
        '''
        embedded = self.onehot_encode(input)
        #if self.if_embedding:
        #    embedded = self.i2e(embedded)
        if self.training and self.dropout_input > 0:
            embedded = self.embedding_dropout(embedded)
        embedded = embedded.unsqueeze(0)  # (1,batch_size, input_size)
        # print(embedded.size())

        # Go through the GRU layer
        output, hidden = self.gru(embedded, hidden)  # (num_layers, batch_size, hidden_size)
        # print(output.size())
        output = output.view(-1, output.size(-1))  # (batch_size, hidden_size)
        # print(output.size())
        logit = self.tanh(self.h2o(output))  # (batch_size, output_size)
        # print(logit.size())

        return logit, hidden
    
    
    def embedding_dropout(self, input):
        p_drop = torch.Tensor(input.size(0), 1).fill_(1 - self.dropout_input)  # (B,1)
        mask = torch.bernoulli(p_drop).expand_as(input)/(1-self.dropout_input) # (B,C)
        mask = mask.to(self.device)
        input = input * mask  # (B,C)
        
        return input
        

    def init_emb(self):
        '''
        Initialize the one_hot embedding buffer, which will be used for producing the one-hot embeddings efficiently
        '''
        onehot_buffer = torch.FloatTensor(self.batch_size, self.output_size)
        onehot_buffer = onehot_buffer.to(self.device)

        return onehot_buffer
    
    
    def onehot_encode(self, input):
        """
        Returns a one-hot vector corresponding to the input

        Args:
            input (B,): torch.LongTensor of item indices
            buffer (B,output_size): buffer that stores the one-hot vector
        Returns:
            one_hot (B,C): torch.FloatTensor of one-hot vectors
        """
        
        self.onehot_buffer.zero_()
        index = input.view(-1,1)
        one_hot = self.onehot_buffer.scatter_(1, index, 1)
        
        return one_hot

    
    def init_hidden(self):
        '''
        Initialize the hidden state of the GRU
        '''
        h0 = torch.zeros(self.num_layers, self.batch_size, self.hidden_size).to(self.device)

        return h0