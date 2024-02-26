import * as React from 'react';
import {useState} from 'react';
import {StyledForm} from "./StyledForm";
import {StyledInput} from "./StyledInput";
import {GreenButton} from "./GreenButton";


interface LoginFormProps {
    slug: string,
    onAuthenticated: (() => void);
}

const LoginForm: React.FC<LoginFormProps> = ({slug, onAuthenticated}) => {
    const [formData, setFormData] = useState({
        passphrase: '',
        slug,
    });

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const {name, value} = e.target;
        setFormData({...formData, [name]: value});
    };

    const handleSubmit = (e: React.FormEvent) => {
        e.preventDefault();
        fetch('/token', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(formData)
        }).then(async response => {
            if (response.status === 200) {
                onAuthenticated();
            } else {
                const json = await response.json();
                if (json.message) {
                    alert(`${response.status}: ${json.message}`);
                } else {
                    alert(`${response.status}: ${JSON.stringify(json)}`);
                }
            }
        });
    };

    return <div>
        <h2>The passphrase is required.</h2>
        <StyledForm onSubmit={handleSubmit}>
            <StyledInput
                name="passphrase"
                type="password"
                placeholder="Enter passphrase"
                value={formData.passphrase}
                onChange={handleChange}
            />
            <GreenButton type="submit">Go to the board</GreenButton>
        </StyledForm>
    </div>;
};

export default LoginForm;
