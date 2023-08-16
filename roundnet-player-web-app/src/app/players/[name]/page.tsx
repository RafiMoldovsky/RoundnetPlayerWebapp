'use client';
import { useSearchParams } from 'next/navigation';

interface pageProps{
  params: {name: string}
}

export default function PlayerPage({params}: pageProps){
  const searchParams = useSearchParams();
  const playerData = JSON.parse(searchParams.get('playerData') || '{}');
  if (playerData === '{}') {
    return null;
  }
  return <div>{playerData.name} {playerData.id}</div>
}