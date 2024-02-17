import * as React from "react";
import {useState} from "react";
import Column from "./Column";
import styled from "styled-components";
import {CardId, CardType, ColumnId, ColumnType} from "../types";
import {getTsid} from "tsid-ts";


const BoardTitle = styled.h2`
  text-align: center;
  margin-top: 20px;
  margin-bottom: 20px;
  font-size: 24px;
  color: #333;
`;

const StyledBoard = styled.div`
  display: flex;
  flex-direction: row;
  justify-content: space-around;
  width: 100%;
  margin: 20px auto;
`;

interface BoardProps {
    title: string;
}

const Board: React.FC<BoardProps> = ({title}) => {
    const [columns, setColumns] = useState<ColumnType[]>([
        {
            id: 1,
            title: 'I\'m glad that...',
            emoji: '😀',
            cards: [],
            color: '#70beb1'
        },
        {
            id: 2,
            title: 'I\'m wondering about...',
            emoji: '🤔',
            cards: [],
            color: '#f5c94c'
        },
        {
            id: 3,
            title: 'It wasn\'t so great that...',
            emoji: '😱',
            cards: [],
            color: '#d35948'
        }
    ]);

    const handleAddExistingCard = (columnId: ColumnId, card: CardType) => {
        setColumns((columns) =>
            columns.map(column => {
                if (column.id === columnId) {
                    return {
                        ...column,
                        cards: [...column.cards, card]
                    };
                }
                return column;
            })
        );
    };
    const handleAddCard = (columnId: ColumnId, text: string, done: boolean) => {
        const card: CardType = {id: getTsid(), text, done, columnId, like: 0};
        handleAddExistingCard(columnId, card);
    };
    const handleUpdateCard = (cardId: CardId, toUpdate: Partial<CardType>) => {
        setColumns(columns => columns.map(column => ({
            ...column,
            cards: column.cards.map(card => {
                const updated = {...card, ...toUpdate};
                return card.id === cardId ? updated : card;
            })
        })));
    };

    const handleDeleteCard = (cardId: CardId) => {
        setColumns(columns => columns.map(column => ({
            ...column,
            cards: column.cards.filter(card => card.id !== cardId)
        })));
    };

    const handleDrag = (columnId: ColumnId, card: CardType) => {
        handleDeleteCard(card.id);
        handleAddExistingCard(columnId, {...card, columnId});
    };

    return (
        <>
            <BoardTitle>{title}</BoardTitle>
            <StyledBoard>
                {columns.map(column => <Column
                    id={column.id}
                    title={column.title}
                    emoji={column.emoji}
                    cards={column.cards}
                    color={column.color}
                    onAddCard={(text) => handleAddCard(column.id, text, false)}
                    onDeleteCard={handleDeleteCard}
                    onUpdateCard={handleUpdateCard}
                    onDrop={handleDrag}
                    key={column.title}/>)}
            </StyledBoard>
        </>
    );
};

export default Board;
