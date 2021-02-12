'use strict'
/*
author: David Xiang
email: xdw@pku.edu.cn
 */

// constant args
const IMAGE_LENGTH = 28;
const INPUT_NODE = 784;
const OUTPUT_NODE = 10;
const NUM_CHANNELS = 1;
const LEARNING_RATE = 0.15;
const TEST_SIZE = 1000;
const HOST = "http://192.168.43.226:8000/"

// tensorflowjs.html?backend=cpu&infertime=10000&model=mnist-1-32
// args to be extracted from url
let task;
let backend;
let inferTime;

let verbose = false;

let backendList = ["cpu", "gpu"];

function getParam(query, key){
    let regex = new RegExp(key+"=([^&]*)","i");
    return query.match(regex)[1];
}


function parseArgs(){
    let address = document.location.href;
    let query = address.split("?")[1];

    backend = getParam(query, "backend");
    //modelname = getParam(query, "model");
    inferTime = parseInt(getParam(query, "infertime"));


    // check whether these params are valid
    if (backendList.indexOf(backend) === -1){
        console.error("Invalid URI:" + address);
        return false;
    }
    return true;
}
