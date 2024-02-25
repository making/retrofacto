import React from 'react'
import ReactDOM from 'react-dom/client'
import Retro from './Retro.tsx'
import {createBrowserRouter, Link, RouterProvider} from "react-router-dom";
import CreateBoardForm from "../components/CreateBoardForm.tsx";
import NavigateToBoardForm from "../components/NavigateToBoardForm.tsx";


const router = createBrowserRouter([
    {
        path: "/",
        element: <div>
            <h1>Retrofacto</h1>
            <NavigateToBoardForm/>
            <p>
                Or <Link to={`/retros`}>create a new board</Link>.
            </p>
        </div>,
    },
    {
        path: "/retros/:slug",
        element: <Retro/>
    },
    {
        path: "/retros",
        element: <CreateBoardForm/>
    }
]);

ReactDOM.createRoot(document.getElementById('root')!).render(
    <React.StrictMode>
        <RouterProvider router={router}/>
    </React.StrictMode>,
)
