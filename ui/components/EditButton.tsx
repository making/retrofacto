import * as React from 'react';
import {StyledButton} from "./StyledButton.tsx";
import {CardId} from "../types";

interface EditButtonProps {
    cardId: CardId;
    onClick: () => void;
    hidden: boolean;
}

const EditButton: React.FC<EditButtonProps> = ({onClick, hidden}) => {
    return (
        <StyledButton onClick={() => onClick()} tabIndex={-1} hidden={hidden}>
            ✏️
        </StyledButton>
    );
};

export default EditButton;
