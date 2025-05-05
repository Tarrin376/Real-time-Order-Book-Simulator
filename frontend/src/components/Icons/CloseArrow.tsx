interface CloseArrowProps {
    state: number
}

function CloseArrow({ state }: CloseArrowProps) {
    if (state == 0) {
        return <></>
    }

    return (
        <svg className={state == -1 ? "arrow-down" : ""} xmlns="http://www.w3.org/2000/svg" height="22px" 
            viewBox="0 -960 960 960" width="22px" fill={state == 1 ? "#4caf50" : "#e53552"}>
            <path d="m280-400 200-200 200 200H280Z"/>
        </svg>
    )
}

export default CloseArrow;