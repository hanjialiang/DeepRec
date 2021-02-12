var __awaiter = (this && this.__awaiter) || function (thisArg, _arguments, P, generator) {
    return new (P || (P = Promise))(function (resolve, reject) {
        function fulfilled(value) { try { step(generator.next(value)); } catch (e) { reject(e); } }
        function rejected(value) { try { step(generator["throw"](value)); } catch (e) { reject(e); } }
        function step(result) { result.done ? resolve(result.value) : new P(function (resolve) { resolve(result.value); }).then(fulfilled, rejected); }
        step((generator = generator.apply(thisArg, _arguments || [])).next());
    });
};
var __generator = (this && this.__generator) || function (thisArg, body) {
    var _ = { label: 0, sent: function() { if (t[0] & 1) throw t[1]; return t[1]; }, trys: [], ops: [] }, f, y, t, g;
    return g = { next: verb(0), "throw": verb(1), "return": verb(2) }, typeof Symbol === "function" && (g[Symbol.iterator] = function() { return this; }), g;
    function verb(n) { return function (v) { return step([n, v]); }; }
    function step(op) {
        if (f) throw new TypeError("Generator is already executing.");
        while (_) try {
            if (f = 1, y && (t = op[0] & 2 ? y["return"] : op[0] ? y["throw"] || ((t = y["return"]) && t.call(y), 0) : y.next) && !(t = t.call(y, op[1])).done) return t;
            if (y = 0, t) op = [op[0] & 2, t.value];
            switch (op[0]) {
                case 0: case 1: t = op; break;
                case 4: _.label++; return { value: op[1], done: false };
                case 5: _.label++; y = op[1]; op = [0]; continue;
                case 7: op = _.ops.pop(); _.trys.pop(); continue;
                default:
                    if (!(t = _.trys, t = t.length > 0 && t[t.length - 1]) && (op[0] === 6 || op[0] === 2)) { _ = 0; continue; }
                    if (op[0] === 3 && (!t || (op[1] > t[0] && op[1] < t[3]))) { _.label = op[1]; break; }
                    if (op[0] === 6 && _.label < t[1]) { _.label = t[1]; t = op; break; }
                    if (t && _.label < t[2]) { _.label = t[2]; _.ops.push(op); break; }
                    if (t[2]) _.ops.pop();
                    _.trys.pop(); continue;
            }
            op = body.call(thisArg, _);
        } catch (e) { op = [6, e]; y = 0; } finally { f = t = 0; }
        if (op[0] & 5) throw op[1]; return { value: op[0] ? op[1] : void 0, done: true };
    }
};
/**
 * @license
 * Copyright 2018 Google LLC. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =============================================================================
 */
var IMAGE_SIZE = 784;
var NUM_CLASSES = 10;
var NUM_DATASET_ELEMENTS = 65000;
var NUM_TRAIN_ELEMENTS = 60000;
var NUM_TEST_ELEMENTS = NUM_DATASET_ELEMENTS - NUM_TRAIN_ELEMENTS;
var MNIST_IMAGES_SPRITE_PATH = "./data/mnist_images.png";
var MNIST_LABELS_PATH = "./data/mnist_labels_uint8";
/**
 * A class that fetches the sprited MNIST dataset and returns shuffled batches.
 *
 * NOTE: This will get much easier. For now, we do data fetching and
 * manipulation manually.
 */
var MnistData = /** @class */ (function () {
    function MnistData() {
        this.shuffledTrainIndex = 0;
        this.shuffledTestIndex = 0;
    }
    MnistData.prototype.load = function () {
        return __awaiter(this, void 0, void 0, function () {
            var img, canvas, ctx, imgRequest, labelsRequest, _a, imgResponse, labelsResponse, _b, _c;
            var _this = this;
            return __generator(this, function (_d) {
                switch (_d.label) {
                    case 0:
                        img = new Image();
                        canvas = document.createElement('canvas');
                        ctx = canvas.getContext('2d');
                        imgRequest = new Promise(function (resolve, reject) {
                            img.crossOrigin = '';
                            img.onload = function () {
                                img.width = img.naturalWidth;
                                img.height = img.naturalHeight;
                                var datasetBytesBuffer = new ArrayBuffer(NUM_DATASET_ELEMENTS * IMAGE_SIZE * 4);
                                var chunkSize = 5000;
                                canvas.width = img.width;
                                canvas.height = chunkSize;
                                for (var i = 0; i < NUM_DATASET_ELEMENTS / chunkSize; i++) {
                                    var datasetBytesView = new Float32Array(datasetBytesBuffer, i * IMAGE_SIZE * chunkSize * 4, IMAGE_SIZE * chunkSize);
                                    ctx.drawImage(img, 0, i * chunkSize, img.width, chunkSize, 0, 0, img.width, chunkSize);
                                    var imageData = ctx.getImageData(0, 0, canvas.width, canvas.height);
                                    for (var j = 0; j < imageData.data.length / 4; j++) {
                                        // All channels hold an equal value since the image is grayscale, so
                                        // just read the red channel.
                                        datasetBytesView[j] = imageData.data[j * 4] / 255;
                                    }
                                }
                                _this.datasetImages = new Float32Array(datasetBytesBuffer);
                                resolve();
                            };
                            img.src = MNIST_IMAGES_SPRITE_PATH;
                        });
                        labelsRequest = fetch(MNIST_LABELS_PATH);
                        return [4 /*yield*/, Promise.all([imgRequest, labelsRequest])];
                    case 1:
                        _a = _d.sent(), imgResponse = _a[0], labelsResponse = _a[1];
                        _b = this;
                        _c = Uint8Array.bind;
                        return [4 /*yield*/, labelsResponse.arrayBuffer()];
                    case 2:
                        _b.datasetLabels = new (_c.apply(Uint8Array, [void 0, _d.sent()]))();
                        // Create shuffled indices into the train/test set for when we select a
                        // random dataset element for training / validation.
                        this.trainIndices = tf.util.createShuffledIndices(NUM_TRAIN_ELEMENTS);
                        this.testIndices = tf.util.createShuffledIndices(NUM_TEST_ELEMENTS);
                        // Slice the the images and labels into train and test sets.
                        this.trainImages =
                            this.datasetImages.slice(0, IMAGE_SIZE * NUM_TRAIN_ELEMENTS);
                        this.testImages = this.datasetImages.slice(IMAGE_SIZE * NUM_TRAIN_ELEMENTS);
                        this.trainLabels =
                            this.datasetLabels.slice(0, NUM_CLASSES * NUM_TRAIN_ELEMENTS);
                        this.testLabels =
                            this.datasetLabels.slice(NUM_CLASSES * NUM_TRAIN_ELEMENTS);
                        return [2 /*return*/];
                }
            });
        });
    };
    MnistData.prototype.nextTrainBatch = function (batchSize) {
        var _this = this;
        return this.nextBatch(batchSize, [this.trainImages, this.trainLabels], function () {
            _this.shuffledTrainIndex =
                (_this.shuffledTrainIndex + 1) % _this.trainIndices.length;
            return _this.trainIndices[_this.shuffledTrainIndex];
        });
    };
    MnistData.prototype.nextTestBatch = function (batchSize) {
        var _this = this;
        return this.nextBatch(batchSize, [this.testImages, this.testLabels], function () {
            _this.shuffledTestIndex =
                (_this.shuffledTestIndex + 1) % _this.testIndices.length;
            return _this.testIndices[_this.shuffledTestIndex];
        });
    };
    MnistData.prototype.nextBatch = function (batchSize, data, index) {
        var batchImagesArray = new Float32Array(batchSize * IMAGE_SIZE);
        var batchLabelsArray = new Uint8Array(batchSize * NUM_CLASSES);
        for (var i = 0; i < batchSize; i++) {
            var idx = index();
            var image = data[0].slice(idx * IMAGE_SIZE, idx * IMAGE_SIZE + IMAGE_SIZE);
            batchImagesArray.set(image, i * IMAGE_SIZE);
            var label = data[1].slice(idx * NUM_CLASSES, idx * NUM_CLASSES + NUM_CLASSES);
            batchLabelsArray.set(label, i * NUM_CLASSES);
        }
        var xs = tf.tensor2d(batchImagesArray, [batchSize, IMAGE_SIZE]);
        var labels = tf.tensor2d(batchLabelsArray, [batchSize, NUM_CLASSES]);
        return { xs: xs, labels: labels };
    };
    return MnistData;
}());
