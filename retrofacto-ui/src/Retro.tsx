import './Retro.css'
import Board from "../components/Board.tsx";
import {DndProvider} from 'react-dnd'
import {HTML5Backend} from 'react-dnd-html5-backend'
import {Link, useParams} from 'react-router-dom';
import {useEffect, useState} from "react";
import LoginForm from "../components/LoginForm.tsx";

const Retro = () => {
    const {slug} = useParams<string>();
    const [name, setName] = useState<string | null>(null);
    const [notFound, setNotFound] = useState<boolean>(false);
    const [authenticationRequired, setAuthenticationRequired] = useState<boolean>(true);

    useEffect(() => {
        fetch(`/boards/${slug}`)
            .then(async response => {
                console.log(`status = ${response.status}`);
                if (response.ok) {
                    console.log('OK');
                    const json = await response.json();
                    setName(json.name);
                    if (json.columns) {
                        setAuthenticationRequired(false);
                    }
                } else if (response.status === 404) {
                    setNotFound(true);
                }
            })
            .catch(() => {
            });
    }, [slug]);
    if (notFound) {
        return <div>
            <p>
                ⁉️The requested retro board is not found.<br/>
                <Link to={`/retros`}>Create a new board</Link>.
            </p>
        </div>;
    } else if (slug) {
        if (authenticationRequired) {
            return <LoginForm slug={slug} onAuthenticated={() => {
                setAuthenticationRequired(false);
            }}/>
        } else if (name) {
            const eventSource = new EventSource(`/boards/${slug}/events`, {withCredentials: true});
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
                        alert('The event source has been closed. Please refresh the page to reload the events.')
                        break;
                }
            };
            return <DndProvider backend={HTML5Backend}>
                <Board name={name} slug={slug} eventSource={eventSource}/>
            </DndProvider>
        }
    } else {
        return <></>;
    }
};

export default Retro
