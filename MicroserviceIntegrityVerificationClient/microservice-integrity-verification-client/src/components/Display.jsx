import { Fragment, useEffect, useState } from "react";

const Display = ({ graphData }) => {
    const [logs, setLogs] = useState(null);

    useEffect(() => {
        console.log(graphData);
        if (graphData) {
            setLogs(getIssues(graphData));
        }
    }, [graphData])

    return <div style={{ backgroundColor: '#404040', height: '100%'}}>
        <div style={{ backgroundColor: '#404040', width: '100%', height: '100%', overflowY: 'auto' }}>
            {logs}
        </div>
    </div>
}

const getIssues = (graphData) => {
    return graphData.messages.map((message, insdex) => 
        <p key={insdex} style={{margin: '5px 50px 5px 50px', color: 'white'}}>{message}</p>)
}

export default Display;