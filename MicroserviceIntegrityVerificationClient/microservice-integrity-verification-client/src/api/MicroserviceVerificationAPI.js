export const getGraph = () => {
    return fetch('http://localhost:8081/api/v1/graph', {
        method: 'GET'
    })
        .then(response => response.json());
}

export const getChangeGraph = (id) => {
    return fetch(`http://localhost:8081/api/v1/change-graph/${id}`, {
        method: 'GET'
    })
        .then(response => response.json());
}

export const getChangeGraphsList = () => {
    return fetch(`http://localhost:8081/api/v1/change-graph`)
        .then(response => response.json());
}

export const getMicroservices = () => {
    return fetch(`http://localhost:8081/api/v1/microservice`)
        .then(response => response.json());
}

export const createChangeGraph = (microservices) => {
    fetch(`http://localhost:8081/api/v1/change-graph`, {
        headers: {
            'Content-Type': 'application/json;charset=utf-8'
        },
        method: 'POST',
        body: JSON.stringify(microservices)
    })
        .catch(error => console.error('Ошибка:', error));
}