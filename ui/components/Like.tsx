import * as React from 'react';
import {useState} from 'react';
import styled from 'styled-components';
import {TSID} from "tsid-ts";

const LikeButton = styled.button`
  background: transparent;
  border: none;
  cursor: ${({hidden}) => hidden ? 'auto' : 'pointer'};
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
    cardId: TSID;
    initial: number;
    setLikeCount: (count: number) => void;
    readonly: boolean;
}

const Like: React.FC<LikeProps> = ({initial, setLikeCount, readonly}) => {
    const [count, setCount] = useState<number>(initial);
    const handleClick = () => {
        if (readonly) {
            return;
        }
        const newCount = count + 1;
        setCount(newCount);
        setLikeCount(newCount);
    };

    return (
        <LikeButton onClick={handleClick} tabIndex={-1} hidden={readonly}>
            ⭐️
            <LikeCount>{count}</LikeCount>
        </LikeButton>
    );
};

export default Like;
