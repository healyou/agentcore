init = {
    println 'masId from java = ' + masId

    type = 'worker'
    name = 'name'
    masId = 'masId'

    println 'masId in groovy = ' + masId
}

onGetMessage = {
    println 'script onGetMessage'
}

onLoadImage = {
    println 'script onLoadImage'
}

onEndImageTask = {
    println 'script onEndImageTask'
}