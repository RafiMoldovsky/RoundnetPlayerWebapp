'use client';
import { useSearchParams } from 'next/navigation';

interface pageProps{
  params: {id: number}
}

export default function PlayerPage({params}: pageProps){
  return <div>{params.id}</div>
}