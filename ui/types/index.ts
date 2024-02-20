export type CardId = string;
export type ColumnId = string;

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
