import * as React from 'react';
import {useState} from 'react';
import styled from 'styled-components';
import {CardId} from "../types";

const LikeButton = styled.button<{ $readOnly: boolean }>`
  background: transparent;
  border: none;
  cursor: ${({$readOnly}) => $readOnly ? 'auto' : 'pointer'};
  font-size: medium;
  margin-right: 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 0;
`;

const LikeCount = styled.div`
  font-size: 0.75em;
  color: #333;
`;

interface LikeProps {
    cardId: CardId;
    initial: number;
    setLikeCount: (count: number) => void;
    readOnly: boolean;
}

const Like: React.FC<LikeProps> = ({initial, setLikeCount, readOnly}) => {
    const [count, setCount] = useState<number>(initial);
    const handleClick = () => {
        if (readOnly) {
            return;
        }
        const newCount = count + 1;
        setCount(newCount);
        setLikeCount(newCount);
    };

    return (
        <LikeButton onClick={handleClick} tabIndex={-1} $readOnly={readOnly}>
            ⭐️
            <LikeCount>{count}</LikeCount>
        </LikeButton>
    );
};

export default Like;
