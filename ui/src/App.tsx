import './App.css'
import Board from "../components/Board.tsx";
import {DndProvider} from 'react-dnd'
import {HTML5Backend} from 'react-dnd-html5-backend'

function App() {

    const slug = "hello";

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
    return (
        <DndProvider backend={HTML5Backend}>
            <Board slug={slug} eventSource={eventSource}/>
        </DndProvider>
    )
}

export default App
