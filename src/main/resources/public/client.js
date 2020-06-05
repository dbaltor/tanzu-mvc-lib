
const callAndAlert = async (URL = '', data = {}, reload = false) => {
    //console.log("URL = " + URL)
    //console.log("data as JSON = " + JSON.stringify(data))
    try {
        const response = await fetch(URL, {
            method: 'POST',
            mode: 'cors',
            cache: 'no-cache',
            headers: {
            'Content-Type': 'application/json'
            },
            body: JSON.stringify(data)
        });
        const text = await response.text()
        alert(text)
        if (reload) {
            location.reload()
        }
    } catch (e) {
        alert(e.text);
    }
}

const borrowBooks = (books) => {
    const readerId = prompt('Please enter the Reader ID.')
    if (readerId) {
        if (isNaN(readerId)) {
            alert('Reader ID must be a whole number.')
            return
        }
        callAndAlert('/readers/' + readerId + '/borrowBooks', books, true)
    }
}

const cleanUpDatabases = () => {
    if (confirm('Are you sure?')) {
        callAndAlert('/cleanup')
    }
}