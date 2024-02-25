import './Retro.css'
import Board from "../components/Board.tsx";
import {DndProvider} from 'react-dnd'
import {HTML5Backend} from 'react-dnd-html5-backend'
import {Link, useParams} from 'react-router-dom';
import {useEffect, useState} from "react";

const Retro = () => {
    const {slug} = useParams<string>();
    const [showBoard, setShowBoard] = useState<boolean>(false);
    const [notFound, setNotFound] = useState<boolean>(false);

    useEffect(() => {
        fetch(`http://localhost:8080/boards/${slug}`)
            .then(async response => {
                console.log(`status = ${response.status}`);
                if (response.ok) {
                    console.log('OK');
                    setShowBoard(true);
                } else if (response.status === 404) {
                    setNotFound(true);
                }
            })
            .catch(() => {
            });
    }, [slug]);

    const eventSource = new EventSource(`http://localhost:8080/boards/${slug}/events`);
    eventSource.onopen = () => {
        console.log('Connected.');
    };
    eventSource.onerror = function (error) {
        const eventSource = error.target as EventSource;
        switch (eventSource.readyState) {
            case EventSource.CONNECTING:
                console.log('Reconnecting...');
                break;
            case EventSource.CLOSED:
                console.log('Closed.')
                break;
        }
    };
    return (slug && (showBoard &&
        <DndProvider backend={HTML5Backend}>
            <Board slug={slug} eventSource={eventSource}/>
        </DndProvider>
    ) || (notFound &&             <div>
        <p>
            ⁉️The requested retro board is not found.<br/>
            <Link to={`/retros`} reloadDocument={true}>Create a new board</Link>.
        </p>
    </div>));
};

export default Retro
