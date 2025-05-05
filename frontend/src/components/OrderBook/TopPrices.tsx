import { useEffect, useState } from "react";
import { Level } from "../../types/Snapshot";

interface TopPrices {
    levels: Level[],
    isBidSide: boolean
}

type SideLevel = Level & {
    total: number
}

function TopPrices({ levels, isBidSide }: TopPrices) {
    const [sideLevels, setSideLevels] = useState<SideLevel[]>([]);
    const [maxVolume, setMaxVolume] = useState<number>(0);

    useEffect(() => {
        let totalSum = 0;
        let maxVol = 0;

        setSideLevels(levels.map(level => {
            totalSum += level.amount;
            maxVol = Math.max(maxVol, level.amount);

            return {
                ...level,
                total: totalSum
            }
        }));

        setMaxVolume(maxVol);
    }, [levels]);

    return (
        <table className="side-table">
            <thead>
                <tr className="table-header">
                    <th>Count</th>
                    <th>Amount</th>
                    <th>Total</th>
                    <th>Price</th>
                </tr>
            </thead>
            <tbody>
                {sideLevels.map((level: SideLevel) => {
                    return (
                        <tr className="side-table-data" key={level.price}>
                            <td>
                                {level.count}
                                <span className={`bg-fill ${isBidSide ? "bg-bid" : "bg-sell"}`} style={{ width: `${(level.amount / maxVolume) * 100}%` }}>
                                </span>
                            </td>
                            <td>{level.amount}</td>
                            <td>{level.total}</td>
                            <td className={isBidSide ? "buy-price" : "sell-price"}>{level.price}</td>
                        </tr>
                    )
                })}
            </tbody>
        </table>
    )
}

export default TopPrices;