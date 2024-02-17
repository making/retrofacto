import * as React from "react";
import {render, screen} from '@testing-library/react';
import '@testing-library/jest-dom';
import Card from '../Card';
import {getTsid} from "tsid-ts";
import {HTML5Backend} from "react-dnd-html5-backend";
import {DndProvider} from "react-dnd";

describe('Card', () => {
    it('renders the text passed to it', () => {
        const testMessage = 'Test Card';
        render(
            <DndProvider backend={HTML5Backend}>
                <Card text={testMessage}
                      id={getTsid()}
                      done={false}
                      columnId={1}
                      like={0}
                      onDelete={() => {
                      }}
                      onUpdate={() => {
                      }}/>
            </DndProvider>
        );
        expect(screen.getByText(testMessage)).toBeInTheDocument();
    });
});
