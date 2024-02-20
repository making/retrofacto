import * as React from 'react';
import {useState} from 'react';
import Card from "./Card";
import styled from "styled-components";
import {CardId, CardType, ColumnId, ColumnType} from "../types";
import {StyledInput} from "./StyledInput.tsx";
import {useDrop} from 'react-dnd'

const StyledColumn = styled.div<{ $isOver: boolean }>`
  flex: 1;
  background-color: ${({color}) => color || '#ffffff'};
  padding: 20px;
  border-radius: 8px;
  opacity: ${({$isOver}) => $isOver ? 0.5 : 1.0};
`;

const StyledForm = styled.form`
  margin-bottom: 10px;
`;

const EmojiIconContainer = styled.div`
  text-align: center;
  font-size: 36px;
`;

interface ColumnProps extends ColumnType {
    onAddCard: ((text: string) => void);
    onDeleteCard: ((cardId: CardId) => void);
    onUpdateCard: ((columnId: ColumnId, cardId: CardId, toUpdate: Partial<CardType>) => void);
    onAddLike: ((cardId: CardId) => void);
    onDrop: ((columnId: ColumnId, card: CardType) => void);
}

const Column: React.FC<ColumnProps> = ({
                                           id,
                                           title,
                                           emoji,
                                           cards,
                                           color,
                                           onAddCard,
                                           onDeleteCard,
                                           onUpdateCard,
                                           onAddLike,
                                           onDrop
                                       }) => {
    const [text, setText] = useState<string>("");
    const handleAddCard = (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        if (!text) return;
        onAddCard(text);
        setText("");
    };
    const [{isOver}, dropRef] = useDrop({
        accept: 'CARD',
        drop: (card: CardType) => {
            onDrop(id, card);
        },
        collect: (monitor) => ({
            isOver: monitor.isOver(),
        }),
    });
    return (
        <StyledColumn color={color} ref={dropRef} $isOver={isOver}>
            <EmojiIconContainer>{emoji}</EmojiIconContainer>
            <StyledForm onSubmit={handleAddCard}>
                <StyledInput
                    type="text"
                    value={text}
                    onChange={(e) => setText(e.target.value)}
                    placeholder={title}
                />
            </StyledForm>
            {cards.map(card => <Card
                    text={card.text}
                    id={card.id}
                    done={card.done}
                    columnId={card.columnId}
                    like={card.like}
                    onDelete={() => onDeleteCard(card.id)}
                    onUpdate={toUpdate => onUpdateCard(card.columnId, card.id, toUpdate)}
                    onAddLike={() => onAddLike(card.id)}
                    key={card.id}
                />
            )}
        </StyledColumn>
    );
};

export default Column;