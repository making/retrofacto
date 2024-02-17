import styled from "styled-components";

export const StyledInput = styled.input`
  width: 100%;
  padding: 15px;
  border: 2px solid #ccc;
  border-radius: 8px;
  box-sizing: border-box;
  font-size: medium;
  margin-top: 10px;
  margin-bottom: 10px;
  &:focus {
    border-color: #aaa;
    outline: none;
  }
`;