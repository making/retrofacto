import * as React from 'react';
import {useState} from 'react';
import styled from 'styled-components';
import Like from "./Like.tsx";
import {CardType} from "../types";
import EditButton from "./EditButton.tsx";
import {StyledInput} from "./StyledInput.tsx";
import DeleteButton from "./DeleteButton.tsx";
import Modal from "./Modal.tsx";
import {useDrag} from 'react-dnd'

const StyledCard = styled.div<{ $done: boolean }>`
  display: flex;
  width: 100%;
  padding: 0 15px 0 15px;
  margin-bottom: 10px;
  background-color: ${({$done}) => $done ? "#ccc" : "#f3f3f3"};
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  cursor: pointer;
  font-size: medium;
  transition: transform 0.2s ease-in-out, background-color 0.2s ease-in-out;
  box-sizing: border-box;
  align-items: center;

  &:hover {
    transform: translateY(-5px);
  }
`;

const StyledForm = styled.form`
  width: 100%;
  margin-right: auto;
`;

const StyledText = styled.span`
  width: 100%;
  padding-bottom: 15px;
  padding-top: 15px;
`;

interface CardTypeProps extends CardType {
    onDelete: (() => void);
    onUpdate: ((toUpdate: Partial<CardType>) => void);
    onAddLike: (() => void);
}

const Card: React.FC<CardTypeProps> = ({id, text, done, columnId, like, onDelete, onUpdate, onAddLike}) => {
    const [isEditing, setIsEditing] = useState<boolean>(false);
    const [txt, setTxt] = useState<string>(text);
    const [isModalOpen, setIsModalOpen] = useState<boolean>(false);

    const startEditing = () => setIsEditing(true);
    const finishEditing = () => setIsEditing(false);
    const openModal = () => setIsModalOpen(true);
    const closeModal = () => setIsModalOpen(false);

    const handleChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        setTxt(event.target.value);
    };

    const handleSubmit = (event: React.FormEvent<HTMLFormElement>) => {
        event.preventDefault();
        onUpdate({text: txt});
        finishEditing();
    };

    const handleDelete = () => {
        const isConfirmed = window.confirm("Are you sure you want to delete this card?");
        if (isConfirmed) {
            onDelete();
        } else {
            finishEditing();
        }
    };

    const handleAddLike = () => {
        onAddLike();
    }

    const markAsDone = () => {
        onUpdate({done: true});
        finishEditing();
        closeModal();
    };
    const redo = () => {
        onUpdate({done: false});
        finishEditing();
        closeModal();
    };

    const Text = () => done ? <del>{text}</del> : <>{text}</>

    const me: CardType = {id, text, done, columnId, like};
    const [{isDragging}, dragRef] = useDrag({
        type: 'CARD',
        item: me,
        collect: (monitor) => ({
            isDragging: monitor.isDragging(),
        }),
    });
    return (
        <>
            <StyledCard $done={done} ref={dragRef} style={{opacity: isDragging ? 0.5 : 1}}>
                <Like cardId={id} like={like} readOnly={done} handleAddLike={handleAddLike}/>
                {isEditing ?
                    <StyledForm onSubmit={handleSubmit}>
                        <StyledInput type="text" value={txt} onChange={handleChange} autoFocus/>
                    </StyledForm> :
                    <StyledText onClick={openModal}><Text/></StyledText>}
                {isEditing ?
                    <DeleteButton onClick={handleDelete} hidden={done}/> :
                    <EditButton cardId={id} onClick={startEditing} hidden={done}/>}
            </StyledCard>
            <Modal isOpen={isModalOpen}
                   done={done}
                   onClose={closeModal}
                   markAsDone={markAsDone}
                   redo={redo}>
                <div>
                    <Like cardId={id} like={like} readOnly={true} handleAddLike={() => {}}/>
                    <p><Text/></p>
                </div>
            </Modal>
        </>
    );
};

export default Card;
