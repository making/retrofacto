import {TSID} from "tsid-ts";

export interface CardType {
    id: TSID;
    text: string;
    done: boolean;
}

export interface ColumnType {
    title: string;
    emoji: string;
    cards: CardType[];
    color: string;
}
