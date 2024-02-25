import * as React from 'react';
import styled from 'styled-components';
import {GreenButton} from "./GreenButton.tsx";

const ModalBackdrop = styled.div`
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: flex-start;
  padding-top: 20vh;
`;

const ModalContent = styled.div`
  background-color: #fff;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  position: relative;
  width: 60%;
  max-width: 640px;
  font-size: x-large;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  min-height: 200px;
`;

const CloseButton = styled.button`
  position: absolute;
  top: 10px;
  right: 10px;
  background: transparent;
  border: none;
  cursor: pointer;
  font-size: 24px;
  color: #000;
`;

const DoneButtonContainer = styled.div`
  text-align: right;
  padding-top: 20px;
`;

interface ModalProps {
    children: React.ReactNode;
    isOpen: boolean;
    done: boolean;
    onClose: () => void;
    markAsDone: () => void;
    redo: () => void;
}

const Modal: React.FC<ModalProps> = ({children, isOpen, done, onClose, markAsDone, redo}) => {
    if (!isOpen) return null;
    return (
        <ModalBackdrop onClick={onClose}>
            <ModalContent onClick={e => e.stopPropagation()}>
                {children}
                <CloseButton onClick={onClose}>×</CloseButton>
                <DoneButtonContainer>
                    {done ?
                        <GreenButton onClick={redo}>Redo</GreenButton> :
                        <GreenButton onClick={markAsDone}>Done</GreenButton>}
                </DoneButtonContainer>
            </ModalContent>
        </ModalBackdrop>
    );
};


export default Modal;
