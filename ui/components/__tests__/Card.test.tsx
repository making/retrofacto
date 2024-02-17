import * as React from "react";
import {render, screen} from '@testing-library/react';
import '@testing-library/jest-dom';
import Card from '../Card';

describe('Card', () => {
    it('renders the text passed to it', () => {
        const testMessage = 'Test Card';
        render(<Card text={testMessage} id={1} done={false} onDelete={() => {
        }}/>);
        expect(screen.getByText(testMessage)).toBeInTheDocument();
    });
});
