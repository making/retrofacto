import * as React from "react";
import {useState} from "react";
import Column from "./Column";
import styled from "styled-components";
import {
    CardCreateEvent,
    CardDeleteEvent,
    CardEvent,
    CardId,
    CardLoadEvent,
    CardType,
    CardUpdateEvent,
    ColumnId,
    ColumnType,
    EventType
} from "../types";

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
    slug: string,
    eventSource: EventSource;
}

const cardSorter = (a: CardType, b: CardType) => {
    if (a.id < b.id) {
        return -1;
    }
    if (a.id > b.id) {
        return 1;
    }
    return 0;
};

const updateCards = (columns: ColumnType[], columnId: ColumnId, getNewCards: ((column: ColumnType) => CardType[])) =>
    columns.map(column => {
        if (column.id === columnId) {
            const newCards = getNewCards(column);
            newCards.sort(cardSorter);
            return {
                ...column,
                cards: newCards
            };
        }
        return column;
    });

const Board: React.FC<BoardProps> = ({slug, eventSource}) => {
    const [name, setName] = useState<string>("...");
    const [columns, setColumns] = useState<ColumnType[]>([]);
    const [emitterId, setEmitterId] = useState<string>('00000000-0000-0000-0000-000000000000');

    eventSource.onmessage = event => {
        const cardEvent = JSON.parse(event.data) as CardEvent;
        const type = cardEvent.type;
        if (type === EventType.LOAD) {
            const {board, emitterId} = cardEvent as CardLoadEvent;
            board.columns.forEach((column: ColumnType) => {
                column.cards.sort(cardSorter);
            });
            setEmitterId(emitterId);
            setName(board.name);
            setColumns(board.columns);
        } else if (type === EventType.CREATE) {
            const {card, columnId} = cardEvent as CardCreateEvent;
            setColumns(updateCards(columns, columnId, column =>
                [...column.cards, card]));
        } else if (type === EventType.UPDATE) {
            const {card: toUpdate, columnId} = cardEvent as CardUpdateEvent;
            setColumns(updateCards(columns, columnId, column =>
                column.cards.map(card => {
                    return card.id === toUpdate.id ? toUpdate : card;
                }))
            );
        } else if (type === EventType.DELETE) {
            const {cardId, columnId} = cardEvent as CardDeleteEvent;
            setColumns(updateCards(columns, columnId, column =>
                column.cards.filter(card => card.id !== cardId)));
        }
    };

    const handleAddExistingCard = (columnId: ColumnId, card: Partial<CardType>) => {
        fetch(`http://localhost:8080/boards/${slug}/cards`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'X-Emitter-Id': emitterId
            },
            body: JSON.stringify({...card, ...{columnId}})
        }).then();
    };
    const handleAddCard = (columnId: ColumnId, text: string, done: boolean) => {
        const card: Partial<CardType> = {text, done, columnId};
        handleAddExistingCard(columnId, card);
    };
    const handleUpdateCard = (columnId: ColumnId, cardId: CardId, toUpdate: Partial<CardType>) => {
        fetch(`http://localhost:8080/boards/${slug}/cards/${cardId}`, {
            method: 'PATCH',
            headers: {
                'Content-Type': 'application/json',
                'X-Emitter-Id': emitterId
            },
            body: JSON.stringify({...toUpdate, ...{columnId}})
        }).then();
    };
    const handleAddLike = (cardId: CardId) => {
        fetch(`http://localhost:8080/boards/${slug}/cards/${cardId}/like`, {
            method: 'POST',
            headers: {
                'X-Emitter-Id': emitterId
            }
        }).then();
    };
    const handleDeleteCard = (cardId: CardId) => {
        fetch(`http://localhost:8080/boards/${slug}/cards/${cardId}`, {
            method: 'DELETE',
            headers: {
                'X-Emitter-Id': emitterId
            }
        }).then();
    };

    const handleDrag = (columnId: ColumnId, card: CardType) => {
        handleDeleteCard(card.id);
        handleAddExistingCard(columnId, {...card, columnId});
    };

    return (
        <>
            <BoardTitle>{name}</BoardTitle>
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
                    onAddLike={handleAddLike}
                    onDrop={handleDrag}
                    key={column.title}/>)}
            </StyledBoard>
        </>
    );
};

export default Board;
