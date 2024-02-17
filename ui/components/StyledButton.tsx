import styled from "styled-components";

export const StyledButton = styled.button`
  background: transparent;
  border: none;
  cursor: pointer;
  font-size: 16px;
  margin-left: auto;
  padding: 0 0 0 15px;
  display: ${({hidden}) => hidden ? 'none' : 'flex'};
  align-items: center;
`;