import * as React from 'react';
import {StyledButton} from "./StyledButton";

interface DeleteButtonProps {
    onClick: () => void;
    hidden: boolean;
}

const DeleteButton: React.FC<DeleteButtonProps> = ({onClick, hidden}) => {
    return (
        <StyledButton onClick={onClick} tabIndex={-1} style={hidden ? {display: 'none'} : {}}>
            🗑️
        </StyledButton>
    );
};

export default DeleteButton;
