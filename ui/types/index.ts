export type CardId = string;
export type ColumnId = string;
export type BoardId = string;

export interface CardType {
    id: CardId;
    text: string;
    done: boolean;
    columnId: ColumnId;
    like: number;
}

export interface ColumnType {
    id: ColumnId,
    title: string;
    emoji: string;
    cards: CardType[];
    color: string;
}

export interface BoardType {
    id: BoardId;
    slug: string;
    name: string;
    passphrase: string | null;
    columns: ColumnType[]
}

export enum EventType {
    LOAD = "LOAD",
    CREATE = "CREATE",
    DELETE = "DELETE",
    UPDATE = "UPDATE",
}

export interface CardEvent {
    type: EventType;
}

export interface CardLoadEvent extends CardEvent {
    board: BoardType;
    emitterId: string;
}

export interface CardCreateEvent extends CardEvent {
    card: CardType;
    columnId: ColumnId;
}

export interface CardUpdateEvent extends CardEvent {
    card: CardType;
    columnId: ColumnId;
}

export interface CardDeleteEvent extends CardEvent {
    cardId: CardId;
    columnId: ColumnId;
}