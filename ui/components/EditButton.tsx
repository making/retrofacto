import * as React from 'react';
import {StyledButton} from "./StyledButton.tsx";
import {TSID} from "tsid-ts";

interface EditButtonProps {
    cardId: TSID;
    onClick: () => void;
    hidden: boolean;
}

const EditButton: React.FC<EditButtonProps> = ({onClick, hidden}) => {
    return (
        <StyledButton onClick={() => onClick()} tabIndex={-1} style={hidden ? {display: 'none'} : {}}>
            ✏️
        </StyledButton>
    );
};

export default EditButton;
