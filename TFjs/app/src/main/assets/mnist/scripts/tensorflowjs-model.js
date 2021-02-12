'use strict';
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
/*
author: David Xiang
email: xdw@pku.edu.cn
 */
var model;
var loadTime, warmupTime;
var avgInferTime;
function infer(modelname) {
    return __awaiter(this, void 0, void 0, function () {
        var start, end, round, totTime, inputTensor, begin, end_1;
        return __generator(this, function (_a) {
            switch (_a.label) {
                case 0:
                    //set backend
                    if (backend == "cpu")
                        tf.setBackend("cpu");
                    else
                        tf.setBackend("webgl");
                    // load model
                    console.log(tf.getBackend());
                    console.log("init model");
                    statusLog("Loading Model");
                    return [4 /*yield*/, tf.loadLayersModel("https://astupidwebsitethatdefinitelydoesnotexist.ai/models/" + modelname + "/model.json")];
                case 1:
                    // load models
                    model = _a.sent();
                    //model = await tf.loadLayersModel(HOST + 'models/' + modelname + "/model.json");
                    window.control.onRefresh("Started!");
                    start = new Date();
                    // warm up the model
                    model.predict(tf.ones([1, INPUT_NODE])).dispose();
                    end = new Date();
                    if (backend == "gpu")
                        warmupTime = end - start;
                    else
                        warmupTime = "cpu";
                    // start inference
                    statusLog("Inferring");
                    round = 0;
                    totTime = 0;
                    inputTensor = tf.ones([1, INPUT_NODE]);
                    while (totTime < inferTime) {
                        inputTensor = inputTensor.add(tf.ones([1, INPUT_NODE]));
                        if (verbose)
                            console.log("Case " + round);
                        begin = new Date();
                        model.predict(inputTensor);
                        end_1 = new Date();
                        totTime += end_1 - begin;
                        round++;
                        if (round % 1000 == 0) {
                            window.control.onRefresh(String(round));
                        }
                    }
                    end = new Date();
                    avgInferTime = totTime / round;
                    statusLog("Finished");
                    console.log(modelname + ": " + round.toString());
                    //let retval = start.toISOString() + "/" + end.toISOString() + "/" + totTime.toString() + "/" + round.toString();
                    window.control.onFinish(round.toString());
                    return [2 /*return*/];
            }
        });
    });
}
function main() {
    return __awaiter(this, void 0, void 0, function () {
        var argsStatus;
        return __generator(this, function (_a) {
            argsStatus = parseArgs();
            if (argsStatus == false)
                return [2 /*return*/];
            statusLog("Ready");
            return [2 /*return*/];
        });
    });
}
main();
