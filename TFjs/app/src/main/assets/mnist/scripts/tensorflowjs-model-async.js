'use strict'
/*
author: David Xiang
email: xdw@pku.edu.cn
 */

let model;
let loadTime, warmupTime;
let avgInferTime;

async function infer(modelname){
    //set backend
    if (backend == "cpu"){
        tf.setBackend("cpu");
    } else {
        tf.setBackend("webgl");
    }

    // load model
    console.log(tf.getBackend());
    console.log("init model");

    statusLog("Loading Model");

    // load models
    model = await tf.loadLayersModel("https://astupidwebsitethatdefinitelydoesnotexist.ai/models/" + modelname + "/model.json");
    //model = await tf.loadLayersModel(HOST + 'models/' + modelname + "/model.json");

    window.control.onFinish("Started!");
    let start = new Date();
    // warm up the model
    model.predict(tf.ones([1, INPUT_NODE])).dispose();
    let end = new Date();
    if (backend == "gpu")
        warmupTime = end - start;
    else
        warmupTime = "cpu"
    
    // start inference
    statusLog("Inferring");

    let round = 0;
    let totTime = 0;
    let inputTensor = tf.ones([1, INPUT_NODE]);
    while(totTime < inferTime){
        inputTensor = inputTensor.add(tf.ones([1, INPUT_NODE]));
        
        if (verbose)
            console.log("Case " + round);

        let begin = new Date();

        model.predict(inputTensor);

        let end = new Date();
        
        totTime += end - begin;
        round++;
        if (round % 5000 == 0){
            window.control.onFinish(String(round));
        }
    }    

    avgInferTime = totTime / round;

    statusLog("Finished");
    console.log(modelname + ": " + round.toString());
    window.control.onFinish(round.toString());
}

async function main(){
    let argsStatus = parseArgs(); // defined in params.js
    if (argsStatus == false)
        return;

    statusLog("Ready");
}
main();

