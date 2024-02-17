import {TSID} from "tsid-ts";


export type CardId = TSID;
export type ColumnId = number;

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
