import { Fragment, useEffect, useState} from "react";
import Graph from "../../components/Graph";
import Display from "../../components/Display";
import * as api from '../../api/MicroserviceVerificationAPI';

const VerificationGraph = () => {
    const [data, setData] = useState(null);

    useEffect(() => {
        api.getGraph().then(data => {
            console.log(data);
            setData(data);
        })
            .catch(err => console.log(err.message));
    }, [])

    return <Fragment>
        <div style={{height: '75vh'}} ><Graph graphData={data} /></div>
        <div style={{height: '25vh'}} ><Display graphData={data} /></div>
    </Fragment>
}

export default VerificationGraph;