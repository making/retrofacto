import * as React from 'react';
import {useState} from 'react';
import {StyledForm} from "./StyledForm";
import {StyledInput} from "./StyledInput";
import {Navigate} from "react-router-dom";


const NavigateToBoardForm: React.FC = () => {
    const [slug, setSlug] = useState<string>('');
    const [forward, setForward] = useState<boolean>(false);

    const handleSubmit = (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        setForward(true);
    };

    return (forward ? <Navigate to={`/retros/${slug}`} replace={true}/> :
            <StyledForm onSubmit={handleSubmit}>
                <StyledInput
                    type="text"
                    placeholder="Enter board slug..."
                    value={slug}
                    onChange={(event: React.ChangeEvent<HTMLInputElement>) => setSlug(event.target.value)}
                />
            </StyledForm>
    );
};

export default NavigateToBoardForm;
