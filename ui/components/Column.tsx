import * as React from 'react';
import {useState} from 'react';
import Card from "./Card";
import styled from "styled-components";
import {CardType, ColumnType} from "../types";
import {StyledInput} from "./StyledInput.tsx";
import {TSID} from "tsid-ts";

const StyledColumn = styled.div`
  flex: 1;
  background-color: ${({color}) => color || '#ffffff'};
  padding: 20px;
  border-radius: 8px;
`;

const StyledForm = styled.form`
  margin-bottom: 10px;
`;

const EmojiIconContainer = styled.div`
  text-align: center;
  font-size: 36px;
`;

interface ColumnProps extends ColumnType {
    onAddCard: ((cardText: string) => void);
    onDeleteCard: ((cardId: TSID) => void);
    onUpdateCard: ((cardId: TSID, toUpdate: Partial<CardType>) => void);
}

const Column: React.FC<ColumnProps> = ({title, emoji, cards, color, onAddCard, onDeleteCard, onUpdateCard}) => {
    const [cardText, setCardText] = useState<string>("");
    const handleAddCard = (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        if (!cardText) return;
        onAddCard(cardText);
        setCardText("");
    };
    return (
        <StyledColumn color={color}>
            <EmojiIconContainer>{emoji}</EmojiIconContainer>
            <StyledForm onSubmit={handleAddCard}>
                <StyledInput
                    type="text"
                    value={cardText}
                    onChange={(e) => setCardText(e.target.value)}
                    placeholder={title}
                />
            </StyledForm>
            {cards.map(card => <Card
                    text={card.text}
                    id={card.id}
                    done={card.done}
                    onDelete={() => onDeleteCard(card.id)}
                    onUpdate={toUpdate => onUpdateCard(card.id, toUpdate)}
                    key={card.id.toBase62String()}
                />
            )}
        </StyledColumn>
    );
};

export default Column;