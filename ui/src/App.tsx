import './App.css'
import Board from "../components/Board.tsx";
import {DndProvider} from 'react-dnd'
import {HTML5Backend} from 'react-dnd-html5-backend'

function App() {

    return (
        <DndProvider backend={HTML5Backend}>
            <Board title={"Retrofacto"}/>
        </DndProvider>
    )
}

export default App
