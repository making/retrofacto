import styled from "styled-components";

export const GreenButton = styled.button`
  background-color: #70beb1;
  color: white;
  padding: 10px 20px;
  border: none;
  font-size: medium;
  border-radius: 5px;
  cursor: pointer;
  transition: background-color 0.3s ease;

  &:hover {
    background-color: #62a89f;
  }

  &:focus {
    outline: none;
  }
`;