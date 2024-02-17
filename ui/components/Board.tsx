import * as React from "react";
import {useState} from "react";
import Column from "./Column";
import styled from "styled-components";
import {CardType, ColumnType} from "../types";
import {getTsid, TSID} from "tsid-ts";


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
            title: 'I\'m glad that...',
            emoji: '😀',
            cards: [],
            color: '#70beb1'
        },
        {
            title: 'I\'m wondering about...',
            emoji: '🤔',
            cards: [],
            color: '#f5c94c'
        },
        {
            title: 'It wasn\'t so great that...',
            emoji: '😱',
            cards: [],
            color: '#d35948'
        }
    ]);

    const handleAddCard = (columnTitle: string, cardText: string) => {
        setColumns((columns) =>
            columns.map(column => {
                if (column.title === columnTitle) {
                    const card = {id: getTsid(), text: cardText, done: false};
                    console.log(card);
                    return {
                        ...column,
                        cards: [...column.cards, card]
                    };
                }
                return column;
            })
        );
    };

    const handleUpdateCard = (cardId: TSID, toUpdate: Partial<CardType>) => {
        setColumns(columns => columns.map(column => ({
            ...column,
            cards: column.cards.map(card => {
                const updated = {...card, ...toUpdate};
                console.log(updated);
                return card.id === cardId ? updated : card;
            })
        })));
    };

    const handleDeleteCard = (cardId: TSID) => {
        setColumns(columns => columns.map(column => ({
            ...column,
            cards: column.cards.filter(card => card.id !== cardId)
        })));
    };

    return (
        <>
            <BoardTitle>{title}</BoardTitle>
            <StyledBoard>
                {columns.map(column => <Column
                    title={column.title}
                    emoji={column.emoji}
                    cards={column.cards}
                    color={column.color}
                    onAddCard={(cardText) => handleAddCard(column.title, cardText)}
                    onDeleteCard={handleDeleteCard}
                    onUpdateCard={handleUpdateCard}
                    key={column.title}/>)}
            </StyledBoard>
        </>
    );
};

export default Board;
